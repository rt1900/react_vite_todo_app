import { mount } from 'cypress/react';
import NoteModal from '../NoteModal';
import '../../App.css';
import '../../index.css';

const mockNote = { id: 1, title: 'Test Title', text: 'Test Text' };

describe('NoteModal.cy.jsx', () => {
    it('renders the modal with note data', () => {
        mount(<NoteModal note={mockNote} onSave={() => {}} onClose={() => {}} />);

        // Проверяем, что поля input и textarea содержат данные заметки
        cy.get('input').should('have.value', mockNote.title);
        cy.get('textarea').should('have.value', mockNote.text);
    });

    it('calls onSave with updated note data', () => {
        const onSaveStub = cy.stub().as('saveStub');
        mount(<NoteModal note={mockNote} onSave={onSaveStub} onClose={() => {}} />);

        // Разделяем цепочки после получения input
        cy.get('input').clear();
        cy.get('input').type('Updated Title');

        cy.get('textarea').clear();
        cy.get('textarea').type('Updated Text');

        // Нажимаем кнопку Save
        cy.get('.save').click();

        // Разделяем цепочку для проверки вызова stub
        cy.get('@saveStub').should('have.been.calledWith', {
            ...mockNote,
            title: 'Updated Title',
            text: 'Updated Text',
        });
    });

    it('calls onClose when close button is clicked', () => {
        const onCloseStub = cy.stub().as('closeStub');
        mount(<NoteModal note={mockNote} onSave={() => {}} onClose={onCloseStub} />);

        // Нажимаем кнопку Close
        cy.get('.close').click();

        // Проверяем, что вызов onClose был сделан
        cy.get('@closeStub').should('have.been.called');
    });
});
