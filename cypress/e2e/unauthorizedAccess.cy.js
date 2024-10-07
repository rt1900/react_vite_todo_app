// cypress/e2e/unauthorizedAccess.cy.js

describe('Unauthorized User Access', () => {
    it('should not display notes to an unauthorized user', () => {
        // Visit the main page of the application
        cy.visit('/');

        // Verify that notes are not displayed
        cy.get('.note-title').should('not.exist');

        // Verify that the login and registration buttons are visible
        cy.get('.loginButton').should('be.visible');
        cy.get('.signinButton').should('be.visible');
    });
});
