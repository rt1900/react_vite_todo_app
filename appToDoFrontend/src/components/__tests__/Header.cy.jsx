import { mount } from '@cypress/react18';
import Header from '../Header.jsx';
import '../../App.css';
import '../../index.css';

describe('Header.cy.jsx', () => {
    it('shows login buttons when not authenticated', () => {
        mount(
            <Header
                isAuthenticated={false}
                onLogin={() => {}}  // Добавляем заглушку для onLogin
                handleRegister={() => {}}  // Добавляем заглушку для handleRegister
                onLogout={() => {}}  // Добавляем заглушку для onLogout
                handleAddNote={() => {}}
            />
        );

        // Проверяем, что кнопки "Sign In" и "Log In" отображаются
        cy.get('.signinButton').should('be.visible').contains('Sign In');
        cy.get('.loginButton').should('be.visible').contains('Log In');
    });

    it('shows email and logout button when authenticated', () => {
        const userEmail = 'test@example.com';
        mount(
            <Header
                isAuthenticated={true}
                userEmail={userEmail}
                onLogout={() => {}}  // Добавляем заглушку для onLogout
                handleRegister={() => {}}  // Добавляем заглушку для handleRegister
                onLogin={() => {}}  // Добавляем заглушку для onLogin
                handleAddNote={() => {}}
            />
        );

        // Проверяем, что email и кнопка "Logout" отображаются
        cy.get('.userEmail').should('be.visible').contains(userEmail);
        cy.get('.logoutButton').should('be.visible').contains('Logout');
    });

    it('displays the logo and text correctly', () => {
        mount(
            <Header
                isAuthenticated={true}
                onLogout={() => {}}  // Добавляем заглушку для onLogout
                handleRegister={() => {}}  // Добавляем заглушку для handleRegister
                onLogin={() => {}}  // Добавляем заглушку для onLogin
                handleAddNote={() => {}}
            />
        );

        // Проверяем, что логотип отображается
        cy.get('img.logo').should('have.attr', 'src');

        // Проверяем, что заголовок METRO.DIGITAL отображается
        cy.get('h1.colorMetro', { timeout: 10000 }).should('be.visible').contains('METRO.DIGITAL');

        // Добавляем задержку, чтобы убедиться, что заголовок успеет загрузиться
        cy.wait(1000);

        // Проверяем, что заголовок "ToDo App" отображается
        cy.get('h2.ToDoAppAndAddNote', { timeout: 10000 }).should('be.visible').contains('TODO');
    });

    it('does not show "Add Note" button when not authenticated', () => {
        const handleAddNote = cy.stub().as('addNoteStub');
        mount(
            <Header
                isAuthenticated={false}
                handleAddNote={handleAddNote}
                onLogout={() => {}}  // Добавляем заглушку для onLogout
                handleRegister={() => {}}  // Добавляем заглушку для handleRegister
                onLogin={() => {}}  // Добавляем заглушку для onLogin
            />
        );

        // Проверяем, что кнопка "Add Note" не отображается
        cy.get('.addNoteButton').should('not.exist');
    });

    it('shows "Add Note" button when authenticated and calls handleAddNote when clicked', () => {
        const handleAddNote = cy.stub().as('addNoteStub');
        mount(
            <Header
                isAuthenticated={true}
                handleAddNote={handleAddNote}
                onLogout={() => {}}  // Добавляем заглушку для onLogout
                handleRegister={() => {}}  // Добавляем заглушку для handleRegister
                onLogin={() => {}}  // Добавляем заглушку для onLogin
            />
        );

        // Проверяем, что кнопка "Add Note" отображается
        cy.get('.addNoteButton').should('be.visible');

        // Нажимаем на кнопку "Add Note"
        cy.get('.addNoteButton').click();

        // Проверяем, что handleAddNote был вызван
        cy.get('@addNoteStub').should('have.been.called');
    });
});
