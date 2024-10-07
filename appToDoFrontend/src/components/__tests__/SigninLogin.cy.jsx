import { mount } from 'cypress/react';
import SigninLogin from '../SigninLogin';
import '../../App.css';
import '../../index.css';

describe('SigninLogin.cy.jsx', () => {

    it('renders the Sign In and Log In buttons', () => {
        mount(<SigninLogin onSignin={() => {}} onLogin={() => {}} />);

        // Проверяем наличие кнопки "Sign In"
        cy.get('.signinButton').should('be.visible').contains('Sign In');

        // Проверяем наличие кнопки "Log In"
        cy.get('.loginButton').should('be.visible').contains('Log In');
    });

    it('opens the registration modal when Sign In is clicked', () => {
        mount(<SigninLogin onSignin={() => {}} onLogin={() => {}} />);

        // Нажимаем на кнопку "Sign In"
        cy.get('.signinButton').click();

        // Проверяем, что открылось окно регистрации
        cy.get('div.SignInModal').should('exist');
    });

    it('closes the registration modal when onClose is called', () => {
        mount(<SigninLogin onSignin={() => {}} onLogin={() => {}} />);

        // Нажимаем на кнопку "Sign In" для открытия модального окна
        cy.get('.signinButton').click();

        // Проверяем, что открылось окно регистрации
        cy.get('div.SignInModal').should('exist');

        // Нажимаем кнопку "Close" внутри модального окна регистрации
        cy.get('button').contains('Close').click();

        // Убедимся, что окно закрывается
        cy.get('div.SignInModal').should('not.exist');
    });

    it('opens the login modal when Log In is clicked', () => {
        mount(<SigninLogin onSignin={() => {}} onLogin={() => {}} />);

        // Нажимаем на кнопку "Log In"
        cy.get('.loginButton').click();

        // Проверяем, что открылось окно логина
        cy.get('div.SignInModal').should('exist');
    });

    it('closes the login modal when onClose is called', () => {
        mount(<SigninLogin onSignin={() => {}} onLogin={() => {}} />);

        // Нажимаем на кнопку "Log In" для открытия модального окна
        cy.get('.loginButton').click();

        // Проверяем, что открылось окно логина
        cy.get('div.SignInModal').should('exist');

        // Нажимаем кнопку "Close" внутри модального окна логина
        cy.get('button').contains('Close').click();

        // Убедимся, что окно закрывается
        cy.get('div.LogInModal').should('not.exist');
    });

    it('calls onSignin when Sign In is submitted', () => {
        const onSigninStub = cy.stub().as('onSigninStub');
        mount(<SigninLogin onSignin={onSigninStub} onLogin={() => {}} />);

        // Открываем окно регистрации
        cy.get('.signinButton').click();

        // Вводим данные в форму регистрации
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="password"]').type('password123');

        // Нажимаем на кнопку "Register"
        cy.get('button[type="submit"]').contains('Register').click();

        // Проверяем, что функция onSignin была вызвана
        cy.get('@onSigninStub').should('have.been.called');
    });

    it('calls onLogin when Log In is submitted', () => {
        const onLoginStub = cy.stub().as('onLoginStub');
        mount(<SigninLogin onSignin={() => {}} onLogin={onLoginStub} />);

        // Открываем окно логина
        cy.get('.loginButton').click();

        // Вводим данные в форму логина
        cy.get('input[type="email"]').type('test@example.com');
        cy.get('input[type="password"]').type('password123');

        // Нажимаем на кнопку "Login"
        cy.get('button[type="submit"]').contains('Login').click();

        // Проверяем, что функция onLogin была вызвана
        cy.get('@onLoginStub').should('have.been.called');
    });
});
