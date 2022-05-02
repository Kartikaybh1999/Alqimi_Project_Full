import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmployeeComponent } from './employee/employee.component';
import { AddEmployeeComponent } from './add-employee/add-employee.component';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { AdminWelcomeComponent } from './admin-welcome/admin-welcome.component';
import { AuthGaurdService } from './service/auth-gaurd.service';
const routes: Routes = [
  {path:'employee', component: EmployeeComponent,canActivate:[AuthGaurdService] },
  {path:'add_employee', component: AddEmployeeComponent,canActivate:[AuthGaurdService] },
  {path:'login', component: LoginComponent},
  {path:'logout', component: LogoutComponent,canActivate:[AuthGaurdService] },
  {path:'admin', component: AdminWelcomeComponent,canActivate:[AuthGaurdService] },
  {path:'', component: EmployeeComponent,canActivate:[AuthGaurdService] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
