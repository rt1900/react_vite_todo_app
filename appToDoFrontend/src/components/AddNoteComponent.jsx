import List from './List';

export default function AddNoteComponent({ notes, handleAddNote, handleDeleteNote, handleNoteClick, handleToggleComplete }) {
    return (
        <div className="content">
            
                <h2>ToDo App</h2>
                <button className="addNoteButton" onClick={handleAddNote}>Add Note</button>
           
            <div> 
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
