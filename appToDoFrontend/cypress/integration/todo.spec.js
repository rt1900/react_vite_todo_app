describe('ToDo App', () => {
    it('successfully loads the application', () => {
        cy.visit('http://localhost:5173'); // Make sure your application is running on this port
        cy.contains('Add Note'); // Verify that the text "Add Note" is present on the page
    });
});
