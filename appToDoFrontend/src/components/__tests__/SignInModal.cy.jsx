import { mount } from 'cypress/react';
import SignInModal from '../SignInModal';
import '../../App.css';
import '../../index.css';

describe('SignInModal.cy.jsx', () => {

    it('renders the modal with correct elements', () => {
        // Монтируем компонент
        mount(<SignInModal onClose={() => {}} onRegister={() => {}} />);

        // Проверяем, что заголовок и кнопки отображаются
        cy.get('h2').contains('Register').should('be.visible');
        cy.get('input[type="email"]').should('be.visible');
        cy.get('input[type="password"]').should('be.visible');
        cy.get('button[type="submit"]').contains('Register').should('be.visible');
        cy.get('button').contains('Close').should('be.visible');
    });

    it('handles email and password input', () => {
        // Монтируем компонент
        mount(<SignInModal onClose={() => {}} onRegister={() => {}} />);

        // Вводим значения в поля email и пароль
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="email"]').should('have.value', 'test@example.com');

        cy.get('input[type="password"]').type('password123');
        cy.get('input[type="password"]').should('have.value', 'password123');
    });

    it('calls onRegister with correct data when form is submitted', () => {
        const onRegisterStub = cy.stub();
        const onCloseStub = cy.stub();

        // Монтируем компонент с заглушками
        mount(<SignInModal onClose={onCloseStub} onRegister={onRegisterStub} />);

        // Вводим значения в поля
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="password"]').type('password123');

        // Нажимаем на кнопку "Register"
        cy.get('button[type="submit"]').click();

        // Проверяем, что onRegister был вызван с правильными данными
        cy.wrap(onRegisterStub).should('have.been.calledWith', { email: 'test@example.com', password: 'password123' });

        // Проверяем, что onClose был вызван после успешной отправки формы
        cy.wrap(onCloseStub).should('have.been.calledOnce');
    });

    it('calls onClose when Close button is clicked', () => {
        const onCloseStub = cy.stub();

        // Монтируем компонент с заглушкой
        mount(<SignInModal onClose={onCloseStub} onRegister={() => {}} />);

        // Нажимаем на кнопку "Close"
        cy.get('button').contains('Close').click();

        // Проверяем, что onClose был вызван
        cy.wrap(onCloseStub).should('have.been.calledOnce');
    });

    it('validates form fields before submission', () => {
        const onRegisterStub = cy.stub();

        // Монтируем компонент
        mount(<SignInModal onClose={() => {}} onRegister={onRegisterStub} />);

        // Пытаемся отправить форму без заполнения полей
        cy.get('button[type="submit"]').click();

        // Проверяем, что onRegister не был вызван, так как поля не заполнены
        cy.wrap(onRegisterStub).should('not.have.been.called');

        // Заполняем только email
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('button[type="submit"]').click();

        // Проверяем, что onRegister все еще не был вызван
        cy.wrap(onRegisterStub).should('not.have.been.called');

        // Теперь заполняем оба поля
        cy.get('input[type="password"]').type('password123');
        cy.get('button[type="submit"]').click();

        // Проверяем, что теперь onRegister вызван
        cy.wrap(onRegisterStub).should('have.been.calledOnce');
    });
});
