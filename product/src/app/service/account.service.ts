import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Customer } from '../model/customer';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();
  private customerIdSubject = new BehaviorSubject<number | null>(null);
  customerId$ = this.customerIdSubject.asObservable();
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {
    // Check for existing session on service initialization
    const storedCustomerId = localStorage.getItem('customerId');
    if (storedCustomerId) {
      this.setLoginStatus(true, parseInt(storedCustomerId));
    } else {
      this.checkSession().subscribe(
        (response) => {
          if (response && response.customerId) {
            this.setLoginStatus(true, response.customerId);
          }
        },
        (error) => {
          console.error('No active session found:', error);
          this.setLoginStatus(false);
        }
      );
    }
  }

  setLoginStatus(status: boolean, customerId?: number) {
    this.isLoggedInSubject.next(status);
    if (status && customerId !== undefined) {
      this.customerIdSubject.next(customerId);
      localStorage.setItem('customerId', customerId.toString());
    } else {
      this.customerIdSubject.next(null);
      localStorage.removeItem('customerId');
    }
  }

  getLoginStatus(): boolean {
    return this.isLoggedInSubject.value;
  }

  getCustomerId(): number | null {
    return this.customerIdSubject.value;
  }

  signUp(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(`${this.baseUrl}/customers`, customer, { withCredentials: true });
  }

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/auth/login`, credentials, { withCredentials: true }).pipe(
      tap(response => {
        if (response && response.customerId) {
          this.setLoginStatus(true, response.customerId);
        }
      })
    );
  }

  getCustomerDetails(customerId: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.baseUrl}/customers/${customerId}`, { withCredentials: true });
  }

  logout(): Observable<any> {
    this.setLoginStatus(false);
    return this.http.post(`${this.baseUrl}/auth/logout`, {}, { withCredentials: true }).pipe(
      tap(
        () => {
          // Optionally clear session data if needed here
          console.log('Logout successful');
        },
        error => {
          console.error('Logout failed:', error);
          throw error; // Rethrow the error to handle it in the component
        }
      )
    );
  }
  

  checkSession(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/auth/session`, { withCredentials: true }).pipe(
      tap(response => {
        if (response && response.customerId) {
          this.setLoginStatus(true, response.customerId);
        } else {
          this.setLoginStatus(false);
        }
      })
    );
  }
}
