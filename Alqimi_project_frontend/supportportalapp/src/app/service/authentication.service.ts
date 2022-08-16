import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { JwtHelperService } from '@auth0/angular-jwt';


@Injectable({providedIn: 'root'})
export class AuthenticationService {
  public host = environment.apiUrl;
  private token: string;
  private loggedInUsername: string;
  private jwtHelper = new JwtHelperService();
  public totalUsers: number =0;
  private usertoken:string;
  
  constructor(private http: HttpClient) {}

  public login(user: User): Observable<HttpResponse<User>> {
 
    return this.http.post<User>(`${this.host}/user/login`, user, { observe: 'response' });
  }
  public login2(user: User): Observable<HttpResponse<User>> {
 
    return this.http.post<User>(`${this.host}/user/login2`, user, { observe: 'response' });
  }

  public register(user: User): Observable<User> {
    return this.http.post<User>(`${this.host}/user/register`, user);
  }

  public logOut(): void {
    // this.loaduserCount();
    // this.totalUsers--;
    // this.saveuserCount(this.totalUsers);
   
    this.token = null;
    this.loggedInUsername = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('users');
  }
  public saveuserCount(totalUsers: any | number):void{
    this.totalUsers = totalUsers;
   localStorage.setItem('totalUsersCount', totalUsers);
  }
  

  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem('token', token);
  }

  public addUserToLocalCache(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getUserFromLocalCache(): User {
    return JSON.parse(localStorage.getItem('user'));
  }

  public loadToken(): void {
    this.token = localStorage.getItem('token');
  }

  public getToken(): string {
    return this.token;
  }
  public loaduserCount():void{
    
    let totalUsers = localStorage.getItem('totalUsers');
  }
 
 

  
  public isUserLoggedIn(): boolean {
    this.loadToken();
  
    if (this.token != null && this.token !== ''){
      if (this.jwtHelper.decodeToken(this.token).sub != null || '') {
        if (!this.jwtHelper.isTokenExpired(this.token)) {
         {
          this.loggedInUsername = this.jwtHelper.decodeToken(this.token).sub;
          return true;
        }
      }
      }
    } else {
      this.logOut();
      return false;
    }
  }
 

}
