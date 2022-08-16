import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { HttpResponse, HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { BehaviorSubject, Subscription } from 'rxjs';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { User } from '../model/user';
import { NotificationType } from '../enum/notification-type.enum';
import { HeaderType } from '../enum/header-type.enum';
import { UserService } from '../service/user.service';
import { FileUploadStatus } from '../model/file-upload.status';
import { SubSink } from 'subsink';
import { NgForm } from '@angular/forms';
import { Role } from '../enum/role.enum';
import { CustomHttpRespone } from '../model/custom-http-response';
import { JwtHelperService } from '@auth0/angular-jwt';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  public showLoading: boolean;
  private subscriptions: Subscription[] = [];
  itsme:boolean=false;
  secondLogin:boolean=false;
  private token: string;
  private titleSubject = new BehaviorSubject<string>('Users');
  public titleAction$ = this.titleSubject.asObservable();
  public users: User[];
  public user: User;
  public refreshing: boolean;
  public selectedUser: User;
  public fileName: string;
  public profileImage: File;
  helper = new JwtHelperService();
  public editUser = new User();
  private currentUsername: string;
  public fileStatus = new FileUploadStatus();
  private subs = new SubSink();
 public localname:any;


  constructor(private router: Router, private authenticationService: AuthenticationService,
              private notificationService: NotificationService,private userService:UserService) {}

  ngOnInit(): void {
   
    

  }

  public onLogin(user: User): void {
    this.showLoading = true;
    this.subscriptions.push(
      this.authenticationService.login(user).subscribe(
        (response: HttpResponse<User>) => {
          
          const token = response.headers.get(HeaderType.JWT_TOKEN);
         const decodedToken = this.helper.decodeToken(token);
         console.log(decodedToken)
         
        


          this.authenticationService.saveToken(token);
          
          this.authenticationService.addUserToLocalCache(response.body);
          this.router.navigateByUrl('/user/management');
    this.showLoading = false;
          
           
          
          
        },
        error=>{
          if(error.status==409)
          {
            this.itsme=true;
          }
        }
      )
    );
  }
  public onLogin2(user: User): void {
    this.showLoading = true;
    this.subscriptions.push(
      this.authenticationService.login2(user).subscribe(
        (response: HttpResponse<User>) => {
          
          const token = response.headers.get(HeaderType.JWT_TOKEN);
         
           if(token==null){
            this.itsme=true;
           }
           else{
          this.authenticationService.saveToken(token);
          
          this.authenticationService.addUserToLocalCache(response.body);
          this.router.navigateByUrl('/user/management');
    this.showLoading = false;
          
           }
          
          
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendErrorNotification(NotificationType.ERROR, errorResponse.error.message);
          this.showLoading = false;
        }
      )
    );
  }

  private sendErrorNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }
  toggleTag(){
    this.itsme=!this.itsme;
  }
  continue(user:User){ 
   
    this.secondLogin=false;
    this.itsme=false;
    this.showLoading = false;
    this.onLogin(user);
    
  }
  cancel(){
    this.itsme=false
    this.showLoading=false;
    this.router.navigateByUrl('/login');
   
   
  }
  // public getUsers(showNotification: boolean): void {
  //   this.refreshing = true;
  //   this.subs.add()
  //   this.subscriptions.push(
  //     this.userService.getUsers().subscribe(
  //       (response: User[]) => {
  //         this.userService.addUsersToLocalCache(response);
  //         this.users = response;
  //         this.refreshing = false;
  //         if (showNotification) {
  //           this.sendNotification(NotificationType.SUCCESS, `${response.length} user(s) loaded successfully.`);
  //         }
  //       },
  //       (errorResponse: HttpErrorResponse) => {
  //         this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
  //         this.refreshing = false;
  //       }
  //     )
  //   );

  // }

  public onSelectUser(selectedUser: User): void {
    this.selectedUser = selectedUser;
    this.clickButton('openUserInfo');
  }

  public onProfileImageChange(event: any): void {
   console.log(event); 
   
  }

  public saveNewUser(): void {
    this.clickButton('new-user-save');
  }

  

  public onEditUser(editUser: User): void {
    this.editUser = editUser;
    this.currentUsername = editUser.username;
    this.clickButton('openUserEdit');
  }

  public searchUsers(searchTerm: string): void {
    const results: User[] = [];
    for (const user of this.userService.getUsersFromLocalCache()) {
      if (user.firstName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.lastName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.userId.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) {
          results.push(user);
      }
    }
    this.users = results;
    if (results.length === 0 || !searchTerm) {
      this.users = this.userService.getUsersFromLocalCache();
    }
  }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN;
  }

  public get isManager(): boolean {
    return this.isAdmin || this.getUserRole() === Role.MANAGER;
  }

  public get isAdminOrManager(): boolean {
    return this.isAdmin || this.isManager;
  }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
