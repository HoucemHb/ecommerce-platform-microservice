import React from 'react';
import * as ReactDOM from 'react-dom/client';
import reactToWebComponent from 'react-to-webcomponent';
import EcommerceDashboard from './EcommerceDashboard.jsx';

// Crée le Web Component
const EcommerceDashboardWC = reactToWebComponent(EcommerceDashboard, React, ReactDOM);

// Définir le tag HTML
customElements.define('ecommerce-dashboard', EcommerceDashboardWC);
