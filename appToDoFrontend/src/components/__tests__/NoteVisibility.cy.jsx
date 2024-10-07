import { mount } from '@cypress/react18'; // Используйте react18, если ваш проект на React 18
import App from '../../App'; // Импорт основного приложения

describe('Note visibility based on roles', () => {

    // Тест для администратора
    it('shows all notes for admin user', () => {
        const adminNotes = [
            { id: 1, title: 'Admin Note 1', text: 'This is the first admin note', userEmail: 'user1@example.com', isCompleted: false },
            { id: 2, title: 'Admin Note 2', text: 'This is the second admin note', userEmail: 'user2@example.com', isCompleted: true },
        ];

        // Создаем фиктивный JWT-токен с ролью администратора
        const adminToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsInJvbGVzIjpbIlJPTEVfQURNSU4iXX0=.signature';

        // Устанавливаем токен и email в localStorage перед монтированием
        cy.window().then((win) => {
            win.localStorage.setItem('token', adminToken);
            win.localStorage.setItem('userEmail', 'admin@example.com');
        });

        // Мокаем ответ от API для администратора
        cy.intercept('GET', 'http://localhost:8080/api/notes', { body: adminNotes }).as('getAdminNotes');

        // Монтируем компонент
        mount(<App />);

        // Ожидаем загрузки заметок
        cy.wait('@getAdminNotes');

        // Проверяем, что все заметки видны
        adminNotes.forEach(note => {
            // Прокручиваем элемент в видимую область и проверяем его
            cy.contains('.note-title', note.title).scrollIntoView().should('be.visible');
            cy.contains('.note-email', note.userEmail).scrollIntoView().should('be.visible'); // Проверяем email пользователя
        });
    });

    // Тест для обычного пользователя
    it('shows only user notes for regular user', () => {
        const userNotes = [
            { id: 1, title: 'User Note 1', text: 'This is the first user note', userEmail: 'user@example.com', isCompleted: false }
        ];

        // Создаем фиктивный JWT-токен с ролью пользователя
        const userToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZXMiOlsiUk9MRV9VU0VSIl19.signature';

        // Устанавливаем токен и email в localStorage перед монтированием
        cy.window().then((win) => {
            win.localStorage.setItem('token', userToken);
            win.localStorage.setItem('userEmail', 'user@example.com');
        });

        // Мокаем ответ от API для обычного пользователя
        cy.intercept('GET', 'http://localhost:8080/api/notes', { body: userNotes }).as('getUserNotes');

        // Монтируем компонент
        mount(<App />);

        // Ожидаем загрузки заметок
        cy.wait('@getUserNotes');

        // Проверяем, что отображаются только заметки этого пользователя
        cy.contains('.note-title', userNotes[0].title).should('be.visible');
        cy.get('.note-email').should('not.exist'); // Проверяем, что email других пользователей не отображается
    });
});
