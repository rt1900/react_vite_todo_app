// cypress/e2e/registration.cy.js

describe('User Registration', () => {
    it('should register a new user successfully', () => {
        cy.visit('/');

        cy.get('.signinButton').click();

        const userEmail = `testuser${Date.now()}@example.com`;
        const password = 'password123';

        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);

        // Intercept the registration request
        cy.intercept('POST', 'http://localhost:8080/api/register').as('registerRequest');

        // Handle the alert window
        cy.on('window:alert', (alertText) => {
            expect(alertText).to.equal('Registration successful! Please log in to continue.');
        });

        cy.get('button[type="submit"]').click();

        // Wait for the registration request to complete
        cy.wait('@registerRequest').its('response.statusCode').should('eq', 200);

        // Click the "Log In" button
        cy.get('.loginButton').click();

        // Verify that the login form is displayed
        cy.get('input[type="email"]').should('be.visible');
        cy.get('input[type="password"]').should('be.visible');

        // Log in using the new credentials
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);

        // Intercept the login request
        cy.intercept('POST', 'http://localhost:8080/api/login').as('loginRequest');

        cy.get('button[type="submit"]').click();

        // Wait for the login request to complete
        cy.wait('@loginRequest').its('response.statusCode').should('eq', 200);

        // Verify that the user successfully logged in
        cy.get('.logoutButton').should('be.visible');

        // Verify that the user's email is displayed
        cy.contains(userEmail).should('be.visible');
    });


    it('should not register a user with an existing email', () => {
        cy.visit('/');

        cy.get('.signinButton').click();

        const existingEmail = 'adminmetro@gmail.com'; // Existing email
        const password = 'password123';

        cy.get('input[type="email"]').type(existingEmail);
        cy.get('input[type="password"]').type(password);

        // Intercept the registration request
        cy.intercept('POST', 'http://localhost:8080/api/register').as('registerRequest');

        // Handle the alert window
        cy.on('window:alert', (alertText) => {
            const expectedMessage = "Ah, you scammer! Trying to register with someone else's email address?!\nThat's it!!! We're calling the FBI!";
            expect(alertText).to.equal(expectedMessage);
        });

        cy.get('button[type="submit"]').click();

        // Wait for the registration request to complete and check the status
        cy.wait('@registerRequest').its('response.statusCode').should('eq', 400);
    });

});
