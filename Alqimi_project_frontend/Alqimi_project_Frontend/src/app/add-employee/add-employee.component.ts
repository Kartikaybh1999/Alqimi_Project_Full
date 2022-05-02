import { Component, OnInit } from '@angular/core';
import { HttpClientService,Employee } from '../service/httpclient.service';
import { MatSnackBar } from '@angular/material/snack-bar';
@Component({
  selector: 'app-add-employee',
  templateUrl: './add-employee.component.html',
  styleUrls: ['./add-employee.component.css']
})
export class AddEmployeeComponent implements OnInit {
  user: Employee = new Employee("","","","");
  constructor(private httpClientService: HttpClientService,private snackBar:MatSnackBar) { }

  ngOnInit(): void {
  }
  createEmployee(): void {
    this.httpClientService.createEmployee(this.user)
        .subscribe( data => {
          let snackBarRef = this.snackBar.open("User Added", 'Dismiss',);
        });

  };
}
