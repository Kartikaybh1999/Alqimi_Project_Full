import { HttpHeaders } from "@angular/common/http";

export class User {
 
  public userId: string;
  public firstName: string;
  public lastName: string;
  public username: string;
  public email: string;
  public lastLoginDate: Date;
  public lastLoginDateDisplay: Date;
  public joinDate: Date;
  public profileImageUrl: string;
  public active: boolean;
  public notLocked: boolean;
  public role: string;
  public authorities: [];
  public isLogged: boolean;
  public isLoggedIn: boolean;
  public tokenJwt:string;
  public oldTokenJwt:string;
  static tokenJwt: any;

  constructor() {
    this.userId = '';
    this.firstName = '';
    this.lastName = '';
    this.username = '';
    this.email = '';
    this.lastLoginDate = null;
    this.lastLoginDateDisplay = null;
    this.joinDate = null;
    this.profileImageUrl = '';
    this.active = false;
    this.notLocked = false;
    this.role = '';
    this.authorities = [];
    this.isLogged=false;
    this.isLoggedIn=false;
    this.tokenJwt;
    this.oldTokenJwt;
  }

}
