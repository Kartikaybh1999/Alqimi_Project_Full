import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../service/authentication.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  isLoading = false;
  username = ''
  password = ''
  invalidLogin = false
  error: string='';

  constructor(private router: Router,
    private loginservice: AuthenticationService,private snackBar:MatSnackBar) { }

  ngOnInit() {
  }

  onSubmit(form: NgForm ) {
    this.isLoading =true;
    (this.loginservice.authenticate(this.username, this.password).subscribe(
      data => {
        this.router.navigate(['/admin'])
        this.invalidLogin = false
        this.isLoading =false;
      },
      errorMessage => {
        this.invalidLogin = true
        this.isLoading =false;
        this.error = errorMessage;
        let snackBarRef = this.snackBar.open(this.error, 'Dismiss',);
      }
    )
    );

  }
}