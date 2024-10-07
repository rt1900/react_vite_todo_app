// cypress/e2e/adminAccess.cy.js

describe('Admin Access Control', () => {
    const adminEmail = 'adminmetro@gmail.com';
    const adminPassword = 'Metro123';
    const userEmail = `testuser${Date.now()}@example.com`;
    const password = 'password123';

    before(() => {
        // Create a new user and a note on their behalf
        cy.visit('/');
        cy.get('.signinButton').click();
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();

        cy.on('window:alert', (text) => {
            expect(text).to.equal('Registration successful! Please log in to continue.');
        });

        // Log in as the new user
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();
        cy.get('.logoutButton').should('be.visible');

        // Create a note on behalf of the new user
        cy.get('.addNoteButton').click();
        cy.get('.box').last().as('newUserNote');
        cy.get('@newUserNote').should('be.visible');
        cy.get('@newUserNote').click();
        cy.get('.modal-content').should('be.visible');
        cy.get('input[placeholder="Title"]').clear().type('Note from another user');
        cy.get('textarea[placeholder="Note"]').clear().type('This note is from another user.');
        cy.get('.save').click();
        cy.contains('.note-title', 'Note from another user').should('be.visible');

        // Log out of the new user's account
        cy.get('.logoutButton').click();
    });

    beforeEach(() => {
        // Log in as an administrator
        cy.visit('/');
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type(adminEmail);
        cy.get('input[type="password"]').type(adminPassword);
        cy.get('button[type="submit"]').click();

        cy.get('.logoutButton').should('be.visible');

        // Create a note on behalf of the administrator if it doesn't exist
        cy.get('.note-title').then((notes) => {
            const adminNoteExists = notes.toArray().some(note => note.innerText.includes('Admin Note'));
            if (!adminNoteExists) {
                cy.get('.addNoteButton').click();
                cy.get('.box').last().as('adminNote');
                cy.get('@adminNote').should('be.visible');
                cy.get('@adminNote').click();
                cy.get('.modal-content').should('be.visible');
                cy.get('input[placeholder="Title"]').clear().type('Admin Note');
                cy.get('textarea[placeholder="Note"]').clear().type('This note is from admin.');
                cy.get('.save').click();
                cy.contains('.note-title', 'Admin Note').should('be.visible');
            }
        });
    });

    afterEach(() => {
        // Log out after each test
        cy.get('.logoutButton').click();
    });

    it('Admin should see all notes from all users with their emails', () => {
        // Verify that the administrator can see notes from all users
        cy.get('.note-email').should('exist');

        // Collect unique email addresses
        cy.get('.note-email').then((emails) => {
            const uniqueEmails = new Set();
            emails.each((index, emailElement) => {
                uniqueEmails.add(emailElement.innerText);
            });
            expect(uniqueEmails.size).to.be.greaterThan(1);
        });
    });

    it('Admin can edit, delete, and mark any note as completed', () => {
        // Create a new note to ensure it exists
        cy.get('.addNoteButton').click();
        cy.get('.box').last().as('newNote');
        cy.get('@newNote').scrollIntoView();  // Scroll to the new note
        cy.get('@newNote').should('be.visible');
        cy.get('@newNote').click();
        cy.get('.modal-content').should('be.visible');
        cy.get('input[placeholder="Title"]').clear().type('Note for Admin Test');
        cy.get('textarea[placeholder="Note"]').clear().type('This note is for testing admin actions.');
        cy.get('.save').click();
        cy.contains('.note-title', 'Note for Admin Test').should('be.visible');

        // Find the created note and assign an alias
        cy.contains('.note-title', 'Note for Admin Test').parents('.box').as('noteToEdit');

        // Edit the note
        cy.get('@noteToEdit').click();
        cy.get('.modal-content').should('be.visible');
        cy.get('input[placeholder="Title"]').clear().type('Note for Admin Test (Edited by Admin)');
        cy.get('.save').click();

        // After saving changes, reassign the edited note
        cy.contains('.note-title', 'Note for Admin Test (Edited by Admin)').parents('.box').as('editedNote');

        // Verify that the changes were saved
        cy.get('@editedNote').contains('Note for Admin Test (Edited by Admin)').should('be.visible');

        // Mark the note as completed
        cy.get('@editedNote').find('.checkbox').click();
        cy.get('@editedNote').find('.checkbox').should('be.checked');

        // Intercept the delete request
        cy.intercept('DELETE', '**/api/notes/*').as('deleteNote');

        // Delete the note
        cy.get('@editedNote').find('.xButton').click();

        // Wait for the delete request to complete
        cy.wait('@deleteNote').its('response.statusCode').should('eq', 204);

        // Verify that the note is removed from the state
        cy.contains('.note-title', 'Note for Admin Test (Edited by Admin)').should('not.exist');
    });



});
