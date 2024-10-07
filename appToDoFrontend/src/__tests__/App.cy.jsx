import { mount } from '@cypress/react18';
import App from '../../src/App';// Импорт основного компонента App

describe('App.cy.jsx', () => {

    it('displays the Header component on load', () => {  // TEST 1
        mount(<App />); // Using the mount function to mount the App component

        // Checking for the presence of the component with the header class
        cy.get('div.header').should('exist'); // Verifying that the element with the header class exists
    });

    // Test for displaying the list of notes when authenticated
    it('displays AddNoteComponent and notes when authenticated', () => {    // TEST 2
        // Mock the request to fetch notes
        cy.intercept('GET', 'http://localhost:8080/api/notes', {
            body: [{ id: 1, title: 'Test Note', text: 'This is a test note', isCompleted: false }],
        }).as('getNotes');

        localStorage.setItem('token', 'mockedToken');
        mount(<App />); // Mount the App component

        cy.wait('@getNotes'); // Wait for the notes request to complete
        cy.get('.addNoteButton').should('exist'); // Check if the "Add Note" button exists
        cy.get('.noteArea2').should('contain', 'Test Note'); // Check if the note is displayed
    });

// Test for correct user registration handling
    it('handles user registration', () => {  // TEST 3
        const userEmail = 'test@example.com';
        const password = 'password123';

        cy.intercept('POST', 'http://localhost:8080/api/register', {
            statusCode: 201,
        }).as('registerRequest');

        mount(<App />);

        cy.get('.signinButton').click(); // Click the registration button
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click(); // Confirm registration

        cy.wait('@registerRequest').its('request.body').should('deep.equal', {
            username: userEmail,
            email: userEmail,
            password,
        });
    });

    // Test for correct login handling
    it('handles user login and loads notes', () => {  // TEST 4
        const userEmail = 'test@example.com';
        const password = 'password123';

        // Mock the login request
        cy.intercept('POST', 'http://localhost:8080/api/login', {
            body: { token: 'mockedToken' }
        }).as('loginRequest');

        // Mock the request to fetch notes
        cy.intercept('GET', 'http://localhost:8080/api/notes', {
            body: [{ id: 1, title: 'Test Note', text: 'This is a test note', isCompleted: false }],
        }).as('getNotes');

        mount(<App />);

        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click(); // Confirm login

        cy.wait('@loginRequest').its('request.body').should('deep.equal', {
            email: userEmail,
            password,
        });

        // Verify that the request for notes was made
        cy.wait('@getNotes');
        cy.get('.noteArea2').should('contain', 'Test Note'); // Verify that the note is displayed
    });


    // Test for correct note addition functionality
    it('handles adding a new note', () => {
        const newNote = { id: 2, title: 'New Note', text: 'This is a new note', isCompleted: false };

        // Mock the request to add a note
        cy.intercept('POST', 'http://localhost:8080/api/notes', {
            body: newNote,
        }).as('addNoteRequest');

        // Mock the request to get all notes
        cy.intercept('GET', 'http://localhost:8080/api/notes', {
            body: [newNote],
        }).as('getNotes');

        // Mock the login request to simulate authentication
        cy.intercept('POST', 'http://localhost:8080/api/login', {
            body: { token: 'fakeToken' }
        }).as('loginRequest');

        mount(<App />);

        // Perform login
        cy.get('.loginButton').click(); // Click the "Log In" button
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="password"]').type('password123');
        cy.get('button[type="submit"]').click();

        // Wait for login to complete
        cy.wait('@loginRequest');

        // Wait for notes to load after login
        cy.wait('@getNotes');

        // Now find the "Add Note" button and add a note
        cy.get('.addNoteButton').click();
        cy.wait('@addNoteRequest').its('request.body').should('deep.equal', {
            title: '',
            text: '',
            isCompleted: false,
        });

        // Verify that the new note is displayed
        cy.get('.noteArea2').should('contain', 'New Note');
    });



    // Test for deleting a note
    it('handles deleting a note', () => {
        const mockNote = { id: 1, title: 'Test Note', text: 'This is a test note', isCompleted: false };

        // Intercept login request and return a mock token
        cy.intercept('POST', 'http://localhost:8080/api/login', {
            body: { token: 'mockedToken' }
        }).as('loginRequest');

        // Intercept the initial GET request to fetch notes and return mockNote
        cy.intercept('GET', 'http://localhost:8080/api/notes', { body: [mockNote] }).as('getNotes');

        // Intercept the request to delete the note
        cy.intercept('DELETE', `http://localhost:8080/api/notes/${mockNote.id}`, {}).as('deleteNoteRequest');

        // Mount the application
        mount(<App />);

        // Perform login
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="password"]').type('password123');
        cy.get('button[type="submit"]').click();

        // Wait for login and fetch notes requests to complete
        cy.wait('@loginRequest');
        cy.wait('@getNotes');

        // Verify that the note is displayed
        cy.get('.box').should('have.length', 1);

        // Click the delete note button
        cy.get('.xButton').click();

        // Wait for the delete note request
        cy.wait('@deleteNoteRequest');

        // Verify that the note is deleted
        cy.get('.box').should('not.exist');
    });



    // Test for updating a note
    it('handles updating a note', () => {
        const mockNote = { id: 1, title: 'Original Title', text: 'Original text', isCompleted: false };
        const updatedNote = { id: 1, title: 'Updated Title', text: 'Updated text', isCompleted: false };

        // Intercept login request and return a mock token
        cy.intercept('POST', 'http://localhost:8080/api/login', {
            body: { token: 'mockedToken' }
        }).as('loginRequest');

        // Intercept request to fetch notes and return mockNote
        cy.intercept('GET', 'http://localhost:8080/api/notes', {
            body: [mockNote]
        }).as('getNotes');

        // Intercept request to update the note
        cy.intercept('PUT', `http://localhost:8080/api/notes/${updatedNote.id}`, {
            body: updatedNote,
        }).as('updateNoteRequest');

        // Mount the application
        mount(<App />);

        // Perform login
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="password"]').type('password123');
        cy.get('button[type="submit"]').click();

        // Wait for login and fetch notes requests to complete
        cy.wait('@loginRequest');
        cy.wait('@getNotes');

        // Ensure the note is displayed
        cy.get('.box').should('have.length', 1);

        // Click the note to edit
        cy.get('.box').click();

        // Wait for the modal window with the class .modal to appear
        cy.get('.modal', { timeout: 10000 }).should('be.visible');

        // Clear the title field
        cy.get('.modal input').clear();

        // Enter a new title
        cy.get('.modal input').type('Updated Title');

        // Clear the text field
        cy.get('.modal textarea').clear();

        // Enter new note text
        cy.get('.modal textarea').type('Updated text');

        // Click the save button
        cy.get('.modal .save').click();

        // Ensure the modal window closes
        cy.get('.modal').should('not.exist');

        // Wait for the update request and verify the request body
        cy.wait('@updateNoteRequest').its('request.body').should('deep.equal', updatedNote);

        // Verify the updated note is displayed with the new title
        cy.get('.box').should('contain', 'Updated Title');
    });


    // Test for handling logout and clearing notes
    it('handles logout and clears notes', () => {
        const mockNotes = [
            { id: 1, title: 'Test Note', text: 'This is a test note', isCompleted: false }
        ];

        // Mock login and fetch notes requests
        cy.intercept('POST', 'http://localhost:8080/api/login', {
            body: { token: 'mockedToken' }
        }).as('loginRequest');

        cy.intercept('GET', 'http://localhost:8080/api/notes', {
            body: mockNotes
        }).as('getNotes');

        // Mount the application and simulate a successful login
        mount(<App />);

        // Perform login
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="password"]').type('password123');
        cy.get('button[type="submit"]').click();

        // Wait for login and fetch notes requests
        cy.wait('@loginRequest');
        cy.wait('@getNotes');

        // Ensure the Logout button is visible, and click it
        cy.get('.logoutButton').should('be.visible').click();

        // Verify that the notes are cleared
        cy.get('.box').should('not.exist');

        // Verify that the token is removed from localStorage
        cy.window().then((win) => {
            expect(win.localStorage.getItem('token')).to.be.null;
        });
    });



});
