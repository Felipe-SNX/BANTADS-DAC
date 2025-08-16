import { APP_INITIALIZER, ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideNgxMask } from 'ngx-mask';
import { MockDataService } from './services/mock-data.service';

export function initializeApp(mockDataService: MockDataService) {
  return () => mockDataService.loadMockData();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes), 
    provideNgxMask(),
    MockDataService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [MockDataService],
      multi: true
    }
  ]
};
