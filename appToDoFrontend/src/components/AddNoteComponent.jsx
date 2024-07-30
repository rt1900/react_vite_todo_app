import List from './List';

export default function AddNoteComponent({ notes, handleDeleteNote, handleNoteClick, handleToggleComplete }) {
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
                    />
                ))} 
            </div>
        </div>
    );
}
