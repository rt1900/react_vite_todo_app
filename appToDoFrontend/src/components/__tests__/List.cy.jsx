import { mount } from 'cypress/react';
import List from '../List';
import '../../App.css';
import '../../index.css';

const mockNote = {
    id: 1,
    title: 'Test Note',
    text: 'This is a test note',
    userEmail: 'user@example.com',
    isCompleted: false,
};

describe('List.cy.jsx', () => {
    it('renders the note correctly', () => {
        mount(
            <List
                note={mockNote}
                handleDeleteNote={() => {}}
                handleNoteClick={() => {}}
                handleToggleComplete={() => {}}
                isAdmin={true}
            />
        );

        cy.get('.note-title').contains(mockNote.title).should('be.visible');
        cy.get('.note-text').contains(mockNote.text).should('be.visible');
    });

    it('calls delete handler when delete button is clicked', () => {
        const handleDeleteNote = cy.stub().as('deleteStub'); // Добавляем alias для лучшей отладки
        const mockNote = { id: 1, title: 'Test Note', text: 'This is a test note', isCompleted: false };

        mount(
            <List
                note={mockNote}
                handleDeleteNote={handleDeleteNote}
                handleNoteClick={() => {}}
                handleToggleComplete={() => {}}
                isAdmin={true}
            />
        );

        cy.get('.xButton').click(); // Кликаем по кнопке удаления
        cy.get('@deleteStub').should('have.been.calledWith', mockNote.id); // Проверяем вызов заглушки
    });



    it('calls note click handler when note is clicked', () => {
        const handleNoteClick = cy.stub().as('noteClickStub');
        const mockNote = { id: 1, title: 'Test Note', text: 'This is a test note', isCompleted: false };

        mount(
            <List
                note={mockNote}
                handleDeleteNote={() => {}}
                handleNoteClick={handleNoteClick}
                handleToggleComplete={() => {}}
                isAdmin={true}
            />
        );

        cy.get('.box').click({ force: true });
        cy.get('@noteClickStub').should('have.been.calledWith', mockNote);
    });



    it('toggles completion when checkbox is clicked', () => {
        const handleToggleComplete = cy.stub().as('toggleCompleteStub');
        const mockNote = { id: 1, title: 'Test Note', text: 'This is a test note', isCompleted: false };

        mount(
            <List
                note={mockNote}
                handleDeleteNote={() => {}}
                handleNoteClick={() => {}}
                handleToggleComplete={handleToggleComplete}
                isAdmin={true}
            />
        );

        cy.get('.checkbox').click();
        cy.get('@toggleCompleteStub').should('have.been.calledWith', mockNote.id, true);
    });


});
