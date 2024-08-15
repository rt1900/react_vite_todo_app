import x from '/red-x-png.png';

export default function List ({ note, onClick, handleDeleteNote, handleNoteClick, handleToggleComplete }) {

    // Новый обработчик для чекбокса
    const handleCheckboxClick = (e) => {
        e.stopPropagation(); // Останавливаю распространение события
        handleToggleComplete(note.id, e.target.checked);
    };

    return (
        <div className='box' onClick={() => handleNoteClick(note)}>

            {/*<div className='Note' >{note.text}</div>*/}

            <div className='note-content'>
                <div className={`note-title ${!note.title ? 'placeholder' : ''}`}>{note.title || 'Title'}</div>
                <div className={`note-text ${!note.text ? 'placeholder' : ''}`}>{note.text || 'Note'}</div>
            </div>

            <div className='boxRightSide'>

                <div>
                    <button className='xButton' onClick={(e) => {
                        e.stopPropagation();
                        handleDeleteNote(note.id);
                    }}>
                    <img src={x} className='xImage' alt="Delete"/>
                    </button>
                </div>

                <div>
                    <input
                        type="checkbox"
                        className='checkbox'
                        checked={note.isCompleted}
                        onClick={handleCheckboxClick}
                    />
                </div>

            </div>

        </div>
    );
}
