// cypress/e2e/login.cy.js

describe('Login Functionality', () => {
    const userEmail = `testuser${Date.now()}@example.com`;
    const password = 'password123';
    const wrongPassword = 'wrongPassword';

    before(() => {
        // Register a new user for testing
        cy.visit('/');
        cy.get('.signinButton').click();
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();

        // Handle alert for successful registration
        cy.on('window:alert', (text) => {
            expect(text).to.equal('Registration successful! Please log in to continue.');
        });
    });

    beforeEach(() => {
        // Before each test, log out if logged in
        cy.visit('/');
        cy.get('body').then(($body) => {
            if ($body.find('.logoutButton').length > 0) {
                // If the logout button exists, click it
                cy.get('.logoutButton').click();
            }
        });
    });

    it('should log in successfully with correct credentials', () => {
        cy.visit('/');
        cy.get('.loginButton').click();

        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();

        // Verify that the user successfully logged in
        cy.get('.logoutButton').should('be.visible');
        cy.contains(userEmail).should('be.visible');
    });

    it('should log out successfully', () => {
        // Log in to the system
        cy.visit('/');
        cy.get('.loginButton').click();

        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();

        // Verify that the user successfully logged in
        cy.get('.logoutButton').should('be.visible');

        // Log out of the system
        cy.get('.logoutButton').click();

        // Verify that after logging out, the "Logout" button is not displayed
        cy.get('.logoutButton').should('not.exist');

        // Verify that the user is redirected to the login or home page
        cy.url().should('eq', Cypress.config().baseUrl + '/'); // Update the URL to match your application

        // Verify that the login and registration buttons are visible again
        cy.get('.loginButton').should('be.visible');
        cy.get('.signinButton').should('be.visible');
    });


    it('should fail to log in with incorrect password', () => {
        cy.visit('/');
        cy.get('.loginButton').click();

        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(wrongPassword);
        cy.get('button[type="submit"]').click();

        // Intercept alert and verify its text
        cy.on('window:alert', (txt) => {
            expect(txt).to.contains('Invalid email or password');
        });

        // Ensure that the user is not logged in
        cy.get('.logoutButton').should('not.exist');
    });


    it('should log in as an admin successfully', () => {
        const adminEmail = 'adminmetro@gmail.com';
        const adminPassword = 'Metro123';

        cy.visit('/');
        cy.get('.loginButton').click();

        cy.get('input[type="email"]').type(adminEmail);
        cy.get('input[type="password"]').type(adminPassword);
        cy.get('button[type="submit"]').click();

        // Verify that the admin successfully logged in
        cy.get('.logoutButton').should('be.visible');
        cy.contains(adminEmail).should('be.visible');

        // Verify that the admin sees additional UI elements
        cy.get('.note-email').should('exist');
    });

});
