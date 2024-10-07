import PropTypes from 'prop-types';
import List from './List';

export default function AddNoteComponent({ notes, handleDeleteNote, handleNoteClick, handleToggleComplete, isAdmin }) {
    return (
        <div className="noteArea1">
            <div className="noteArea2">
                {notes.map(note => (
                    <List 
                        key={note.id} 
                        note={note} 
                        handleDeleteNote={handleDeleteNote}
                        handleNoteClick={handleNoteClick}
                        handleToggleComplete={handleToggleComplete}
                        isAdmin={isAdmin} // Passing information whether the user is an admin
                    />
                ))} 
            </div>
        </div>
    );
}

AddNoteComponent.propTypes = {
    notes: PropTypes.array.isRequired,
    handleDeleteNote: PropTypes.func.isRequired,
    handleNoteClick: PropTypes.func.isRequired,
    handleToggleComplete: PropTypes.func.isRequired,
    isAdmin: PropTypes.bool.isRequired,
};
