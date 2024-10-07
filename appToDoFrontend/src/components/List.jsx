import PropTypes from 'prop-types';
import x from '/red-x-png.png';

export default function List ({ note, /*onClick,*/ handleDeleteNote, handleNoteClick, handleToggleComplete, isAdmin }) {

    // New handler for the checkbox
    const handleCheckboxClick = (e) => {
        e.stopPropagation(); // Stopping event propagation
        handleToggleComplete(note.id, e.target.checked);
    };

    // Formatting the date for readable display (e.g., DD.MM.YYYY)
    const formattedDate = new Date(note.lastUpdated).toLocaleDateString('ru-RU');

    return (


        <div className='box' onClick={() => handleNoteClick(note)}>
            {/* Show email only if the user is an admin */}
            {/* Display email on top or on the right */}

            <div className='note-content'>
                <div className={`note-title ${!note.title ? 'placeholder' : ''}`}>{note.title || 'Title'}</div>
                <div className={`note-text ${!note.text ? 'placeholder' : ''}`}>{note.text || 'Note'}</div>
            </div>

            <div className='boxRightSide'>

                    {isAdmin && note.userEmail && (
                        <div className='note-email'>{note.userEmail}</div> // Adding the user's email
                    )}
                <div className="boxRightSideIN">
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
                    <div className="data">{formattedDate}</div>
                </div>


            </div>

        </div>
    );
}


List.propTypes = {
    note: PropTypes.shape({
        id: PropTypes.number.isRequired,
        title: PropTypes.string.isRequired,
        text: PropTypes.string,
        userEmail: PropTypes.string,
        lastUpdated: PropTypes.string.isRequired,
        isCompleted: PropTypes.bool,
    }).isRequired,
    onClick: PropTypes.func, // Add validation for onClick
    handleDeleteNote: PropTypes.func.isRequired,
    handleNoteClick: PropTypes.func.isRequired,
    handleToggleComplete: PropTypes.func.isRequired,
    isAdmin: PropTypes.bool.isRequired,
};