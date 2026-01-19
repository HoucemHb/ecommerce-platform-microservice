import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import './react/EcommerceDashboardWC.js'; // <-- Web Component

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet], // <-- juste RouterOutlet, pas besoin de ReactWrapperModule
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  schemas: [CUSTOM_ELEMENTS_SCHEMA], // nécessaire pour le Web Component
})
export class AppComponent {
  title = 'ecommerce-app';
}