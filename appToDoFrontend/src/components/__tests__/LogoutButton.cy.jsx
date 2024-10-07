import { mount } from 'cypress/react';
import LogoutButton from '../LogoutButton';
import '../../App.css';
import '../../index.css';

describe('LogoutButton.cy.jsx', () => {
    const userEmail = 'test@example.com';

    it('displays the user email correctly', () => {
        mount(<LogoutButton onLogout={() => {}} userEmail={userEmail} />);

        cy.get('.userEmail').should('be.visible').and('contain', userEmail);
    });

    it('displays the logout button', () => {
        mount(<LogoutButton onLogout={() => {}} userEmail={userEmail} />);

        cy.get('.logoutButton').should('be.visible').and('contain', 'Logout');
    });

    it('calls onLogout when the logout button is clicked', () => {
        const onLogoutStub = cy.stub().as('onLogoutStub');

        mount(<LogoutButton onLogout={onLogoutStub} userEmail={userEmail} />);

        cy.get('.logoutButton').click();
        cy.get('@onLogoutStub').should('have.been.calledOnce');
    });
});
