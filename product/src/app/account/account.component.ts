import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../service/account.service';
import { Router } from '@angular/router';
import { Customer } from '../model/customer';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {
  isLoggedIn = false;
  user: Customer | null = null;
  isSignUpMode = true;

  signUpForm: FormGroup;
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private router: Router
  ) {
    this.signUpForm = this.fb.group({
      firstname: ['', Validators.required],
      middlename: [''],
      lastname: ['', Validators.required],
      dateOfBirth: [
        '',
        [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)] // YYYY-MM-DD format
      ],
      gender: ['', Validators.required],
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit() {
    // Subscribe to isLoggedIn observable
    this.accountService.isLoggedIn$.subscribe(isLoggedIn => {
        this.isLoggedIn = isLoggedIn;
        if (isLoggedIn) {
            // Load user details if logged in
            this.accountService.customerId$.subscribe(customerId => {
                if (customerId) {
                    this.accountService.getCustomerDetails(customerId).subscribe(user => {
                        this.user = user;
                    });
                }
            });
        } else {
            this.user = null; // Clear user data on logout
        }
    });
}

  fetchUserDetails(customerId: number) {
    this.accountService.getCustomerDetails(customerId).subscribe(
      (user: Customer) => {
        this.user = user;
      },
      (error) => {
        console.error('Error fetching user details:', error);
        alert('Failed to load user details.');
        this.onLogout(); // Logout if user details could not be fetched
      }
    );
  }

  onSignUp() {
    console.log('Sign up button clicked');
    if (this.signUpForm.valid) {
      this.accountService.signUp(this.signUpForm.value).subscribe(
        (response: Customer) => {
          console.log('Sign-up successful:', response);
          alert('Account created successfully!');
          this.isSignUpMode = false; // Switch to login mode after sign-up
          this.signUpForm.reset(); // Clear form after success
        },
        (error) => {
          console.error('Sign-up failed:', error);
          alert('Sign-up failed: ' + error.message);
        }
      );
    } else {
      console.warn('Sign-up form is invalid');
    }
  }

  onLogin() {
    console.log('Login button clicked');
    if (this.loginForm.valid) {
        this.accountService.login(this.loginForm.value).subscribe(
            (response) => {
                if (response && response.customerId) {
                    console.log('Login successful:', response);
                    alert('Login successful!');
                    this.isLoggedIn = true;
                    this.accountService.setLoginStatus(true, response.customerId);

                    // Explicitly fetch the new user's details after setting the login status
                    this.accountService.customerId$.subscribe(customerId => {
                        if (customerId) {
                            this.fetchUserDetails(customerId);
                        }
                    });

                    // Redirect after login
                    this.router.navigate(['/home']); 
                } else {
                    alert('Login failed: No customer ID returned');
                    console.error('No customer ID returned in response:', response);
                }
            },
            (error) => {
                console.error('Login failed:', error);
                alert('Login failed: ' + error.message);
            }
        );
    } else {
        console.warn('Login form is invalid');
    }
}


  onLogout() {
    this.accountService.logout().subscribe(
        () => {
            alert('Logged out successfully!'); // Show alert first
            this.router.navigate(['/account']); // Then redirect
            this.user = null; // Clear user data in the component
            this.isLoggedIn = false; // Update the login state
        },
        (error) => {
            alert('Logout failed: ' + error.message);
            console.error('Logout error:', error);
        }
    );
}

  




  toggleMode() {
    this.isSignUpMode = !this.isSignUpMode;
  }
}
