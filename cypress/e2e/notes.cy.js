// cypress/e2e/notes.cy.js

describe('Notes Management', () => {
    const userEmail = `testuser${Date.now()}@example.com`;
    const password = 'password123';

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
        // Log in before each test
        cy.visit('/');
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();

        // Ensure that the user is logged in
        cy.get('.logoutButton').should('be.visible');
    });

    afterEach(() => {
        // Log out after each test
        cy.get('.logoutButton').click();
    });

    it('should create a new note', () => {
        // Click the "Add Note" button
        cy.get('.addNoteButton').click();

        // Find the new empty note (with placeholders)
        cy.get('.box').contains('.note-title', 'Title').should('be.visible');

        // Click on the new note to open the modal window
        cy.get('.box').contains('.note-title', 'Title').click();

        // Verify that the modal window is open
        cy.get('.modal-content').should('be.visible');

        // Fill in the note details
        cy.get('input[placeholder="Title"]').clear().type('My New Note');
        cy.get('textarea[placeholder="Note"]').clear().type('This is the content of my new note.');

        // Save the note
        cy.get('.save').click();

        // Verify that the note is displayed with the updated title
        cy.contains('.note-title', 'My New Note').should('be.visible');
    });

    it('should edit an existing note', () => {
        // Ensure the note exists or create it
        cy.get('.addNoteButton').click();
        cy.get('.box').contains('.note-title', 'Title').click();
        cy.get('.modal-content').should('be.visible');
        cy.get('input[placeholder="Title"]').clear().type('My New Note');
        cy.get('textarea[placeholder="Note"]').clear().type('This is the content of my new note.');
        cy.get('.save').click();
        cy.contains('.note-title', 'My New Note').should('be.visible');

        // Now edit the created note
        cy.contains('.note-title', 'My New Note').click();
        cy.get('.modal-content').should('be.visible');

        // Change the title and content
        cy.get('input[placeholder="Title"]').clear().type('Updated Note Title');
        cy.get('textarea[placeholder="Note"]').clear().type('Updated note content.');
        cy.get('.save').click();

        // Verify that the changes are displayed
        cy.contains('.note-title', 'Updated Note Title').should('be.visible');
    });

    it('should mark a note as completed', () => {
        // Create a new note
        cy.get('.addNoteButton').click();

        // Find the new empty note and open it for editing
        cy.get('.box').contains('.note-title', 'Title').click();

        // Fill in the note details
        cy.get('.modal-content').should('be.visible');
        cy.get('input[placeholder="Title"]').clear().type('Note to Complete');
        cy.get('textarea[placeholder="Note"]').clear().type('This note will be marked as completed.');
        cy.get('.save').click();

        // Ensure the note is displayed with the new title
        cy.contains('.note-title', 'Note to Complete').should('be.visible');

        // Find the note and mark it as completed
        cy.contains('.note-title', 'Note to Complete')
            .parents('.box')
            .find('.checkbox')
            .click();

        // Verify that the checkbox is checked
        cy.contains('.note-title', 'Note to Complete')
            .parents('.box')
            .find('.checkbox')
            .should('be.checked');
    });

    it('should delete a note', () => {
        // Create a new note
        cy.get('.addNoteButton').click();

        // Find the new empty note and open it for editing
        cy.get('.box').contains('.note-title', 'Title').click();

        // Fill in the note details
        cy.get('.modal-content').should('be.visible');
        cy.get('input[placeholder="Title"]').clear().type('Note to Delete');
        cy.get('textarea[placeholder="Note"]').clear().type('This note will be deleted.');
        cy.get('.save').click();

        // Ensure the note is displayed with the new title
        cy.contains('.note-title', 'Note to Delete').should('be.visible');

        // Find the note and click the delete button
        cy.contains('.note-title', 'Note to Delete')
            .parents('.box')
            .find('.xButton')
            .click();

        // Handle any delete confirmation if it exists
        // For example, if a confirmation alert appears:
        // cy.on('window:confirm', () => true);

        // Verify that the note is deleted
        cy.contains('.note-title', 'Note to Delete').should('not.exist');
    });


});
