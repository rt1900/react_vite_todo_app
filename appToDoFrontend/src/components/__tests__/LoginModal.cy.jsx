import { mount } from 'cypress/react';
import LoginModal from '../LogInModal';
import '../../App.css';
import '../../index.css';

describe('LoginModal.cy.jsx', () => {
    it('renders the modal with correct elements', () => {
        mount(<LoginModal onClose={() => {}} onLogin={() => {}} />);

        // Проверяем заголовок "Login"
        cy.get('h2').contains('Login').should('be.visible');

        // Проверяем наличие полей для email и password
        cy.get('input[type="email"]').should('be.visible');
        cy.get('input[type="password"]').should('be.visible');

        // Проверяем наличие кнопки "Login"
        cy.get('button[type="submit"]').contains('Login').should('be.visible');

        // Проверяем наличие кнопки "Close"
        cy.get('button').contains('Close').should('be.visible');
    });

    it('handles email and password input', () => {
        mount(<LoginModal onClose={() => {}} onLogin={() => {}} />);

        // Вводим email и проверяем его наличие в поле
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="email"]').should('have.value', 'test@example.com');

        // Вводим пароль и проверяем его наличие в поле
        cy.get('input[type="password"]').type('password123');
        cy.get('input[type="password"]').should('have.value', 'password123');
    });

    it('calls onLogin with correct data when form is submitted', () => {
        const onLoginStub = cy.stub().as('onLoginStub');
        mount(<LoginModal onClose={() => {}} onLogin={onLoginStub} />);

        // Вводим данные в поля
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="password"]').type('password123');

        // Нажимаем кнопку "Login"
        cy.get('button[type="submit"]').click();

        // Проверяем, что onLogin была вызвана с правильными параметрами
        cy.get('@onLoginStub').should('have.been.calledWith', {
            email: 'test@example.com',
            password: 'password123'
        });
    });

    it('calls onClose when Close button is clicked', () => {
        const onCloseStub = cy.stub().as('onCloseStub');
        mount(<LoginModal onClose={onCloseStub} onLogin={() => {}} />);

        // Нажимаем кнопку "Close"
        cy.get('button').contains('Close').click();

        // Проверяем, что onClose была вызвана
        cy.get('@onCloseStub').should('have.been.calledOnce');
    });

    it('validates form fields before submission', () => {
        const onLoginStub = cy.stub().as('onLoginStub');
        mount(<LoginModal onClose={() => {}} onLogin={onLoginStub} />);

        // Оставляем поля пустыми и проверяем, что отправка не происходит
        cy.get('button[type="submit"]').click();
        cy.get('@onLoginStub').should('not.have.been.called');

        // Вводим только email, проверяем, что отправка не происходит
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('button[type="submit"]').click();
        cy.get('@onLoginStub').should('not.have.been.called');

        // Вводим и email, и пароль, проверяем, что отправка происходит
        cy.get('input[type="password"]').type('password123');
        cy.get('button[type="submit"]').click();
        cy.get('@onLoginStub').should('have.been.calledOnce');
    });
});
