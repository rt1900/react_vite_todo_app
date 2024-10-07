describe('User Access Control', () => {
    const userEmail = `user${Date.now()}@example.com`;
    const password = 'password123';
    const otherUserEmail = `otheruser${Date.now()}@example.com`;
    const otherPassword = 'password123';

    before(() => {
        // Register two users and create notes on their behalf

        // User 1
        cy.visit('/');
        cy.get('.signinButton').click();
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();

        cy.on('window:alert', (text) => {
            expect(text).to.equal('Registration successful! Please log in to continue.');
        });

        // Входим и создаём заметку
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();

        cy.get('.addNoteButton').click();
        cy.get('.box').last().as('userNote');
        cy.get('@userNote').click();
        cy.get('.modal-content').should('be.visible');
        cy.get('input[placeholder="Title"]').clear().type('User Note');
        cy.get('textarea[placeholder="Note"]').clear().type('This is a note from user.');
        cy.get('.save').click();
        cy.contains('.note-title', 'User Note').should('be.visible');

        cy.get('.logoutButton').click();

        // Пользователь 2
        cy.get('.signinButton').click();
        cy.get('input[type="email"]').type(otherUserEmail);
        cy.get('input[type="password"]').type(otherPassword);
        cy.get('button[type="submit"]').click();

        cy.on('window:alert', (text) => {
            expect(text).to.equal('Registration successful! Please log in to continue.');
        });

        // Log in and create a note
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type(otherUserEmail);
        cy.get('input[type="password"]').type(otherPassword);
        cy.get('button[type="submit"]').click();

        cy.get('.addNoteButton').click();
        cy.get('.box').last().as('otherUserNote');
        cy.get('@otherUserNote').click();
        cy.get('.modal-content').should('be.visible');
        cy.get('input[placeholder="Title"]').clear().type('Other User Note');
        cy.get('textarea[placeholder="Note"]').clear().type('This is a note from other user.');
        cy.get('.save').click();
        cy.contains('.note-title', 'Other User Note').should('be.visible');

        cy.get('.logoutButton').click();
    });

    beforeEach(() => {
        // Log in as the first user before each test
        cy.visit('/');
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();
    });

    afterEach(() => {
        // Log out after each test
        cy.get('.logoutButton').click();
    });

    it('User should see only their own notes', () => {
        // Verify that the other user's note is not displayed
        cy.contains('.note-title', 'Other User Note').should('not.exist');

        // Verify that the user's own note is displayed
        cy.contains('.note-title', 'User Note').should('be.visible');
    });

    it('User cannot access or delete notes of other users via API', () => {
        // Get the ID of another user's note (using admin privileges)
        let otherNoteId;

        // Log in as the admin
        cy.visit('/');
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type('adminmetro@gmail.com');
        cy.get('input[type="password"]').type('Metro123');
        cy.get('button[type="submit"]').click();

        // Find the other user's note and retrieve its ID
        cy.contains('.note-title', 'Other User Note')
            .parents('.box')
            .then($note => {
                otherNoteId = $note.attr('data-note-id');
            });

        cy.get('.logoutButton').click();

        // Log in as the first user
        cy.get('.loginButton').click();
        cy.get('input[type="email"]').type(userEmail);
        cy.get('input[type="password"]').type(password);
        cy.get('button[type="submit"]').click();

        // Attempt to access the other user's note via API
        const token = localStorage.getItem('token');
        cy.request({
            method: 'GET',
            url: `http://localhost:8080/api/notes/${otherNoteId}`,
            headers: {
                Authorization: `Bearer ${token}`,
            },
            failOnStatusCode: false,
        }).then((response) => {
            expect(response.status).to.equal(403); // Expect Forbidden status
        });

        // Attempt to delete the other user's note via API
        cy.request({
            method: 'DELETE',
            url: `http://localhost:8080/api/notes/${otherNoteId}`,
            headers: {
                Authorization: `Bearer ${token}`,
            },
            failOnStatusCode: false,
        }).then((response) => {
            expect(response.status).to.equal(403); // Expect Forbidden status
        });
    });

});
