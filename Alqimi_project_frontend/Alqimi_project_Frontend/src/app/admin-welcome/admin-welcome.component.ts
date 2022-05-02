import { Component, OnInit } from '@angular/core';
import { HttpClientService,Employee } from '../service/httpclient.service';
@Component({
  selector: 'app-admin-welcome',
  templateUrl: './admin-welcome.component.html',
  styleUrls: ['./admin-welcome.component.css']
})
export class AdminWelcomeComponent implements OnInit {

  constructor(private httpClientService: HttpClientService) { }

  ngOnInit(): void {
  }

}
