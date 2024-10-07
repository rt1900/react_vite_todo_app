import { mount } from '@cypress/react18';
import AddNoteComponent from '../AddNoteComponent.jsx';
import '../../App.css';
import '../../index.css';

const mockData = [
  { id: 1, title: 'First Note', text: 'This is the first note', userEmail: 'user@example.com', isCompleted: false },
  { id: 2, title: 'Second Note', text: 'This is the second note', userEmail: 'user@example.com', isCompleted: false },
];

describe('AddNoteComponent.cy.jsx', () => {
  it('renders correctly', () => {
    // Устанавливаем размер окна браузера
    cy.viewport(1280, 800); // Ширина 1280px, высота 800px

    // Монтируем компонент внутри контейнера с достаточными размерами
    mount(
        <div style={{ width: '1280px', height: '800px' }}>
          <AddNoteComponent
              notes={mockData}
              handleDeleteNote={() => {}}
              handleNoteClick={() => {}}
              handleToggleComplete={() => {}}
              isAdmin={true}
          />
        </div>
    );

    // Попробуем пропустить шаг прокрутки или отключим проверку, чтобы избежать проблемы с не прокручиваемым элементом
    cy.get('.noteArea2').scrollTo('top', { ensureScrollable: false });

    // Делаем скриншот для отладки (опционально)
    cy.screenshot('before-assertions');

    // Проверяем, что количество элементов соответствует ожидаемому
    cy.get('.note-title').should('have.length', mockData.length);
    cy.get('.note-text').should('have.length', mockData.length);

    // Проверяем, что каждый заголовок заметки видим и содержит правильный текст
    cy.get('.note-title').each(($el, index) => {
      cy.wrap($el).should('contain.text', mockData[index].title);
      cy.wrap($el).scrollIntoView().should('be.visible');
    });

    // Проверяем, что каждый текст заметки видим и содержит правильный текст
    cy.get('.note-text').each(($el, index) => {
      cy.wrap($el).should('contain.text', mockData[index].text);
      cy.wrap($el).scrollIntoView().should('be.visible');
    });
  });

  it('deletes a note when delete button is clicked', () => {
    const handleDeleteNote = cy.stub(); // Создаем заглушку для функции удаления

    // Массив с заметками
    const mockData = [
      { id: 1, title: 'First Note', text: 'This is the first note', userEmail: 'user@example.com', isCompleted: false }
    ];

    // Монтируем компонент с заглушкой
    mount(
        <AddNoteComponent
            notes={mockData}
            handleDeleteNote={handleDeleteNote} // Передаем заглушку
            handleNoteClick={() => {}}
            handleToggleComplete={() => {}}
            isAdmin={true}
        />
    );

    // Проверим, что кнопка отображается правильно
    cy.get('.xButton').should('be.visible');

    // Кликаем по кнопке удаления
    cy.get('.xButton').click();

    // Проверяем, что функция удаления была вызвана с правильным id
    cy.wrap(handleDeleteNote).should('have.been.calledWith', mockData[0].id);
  });
});
