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
                        isAdmin={isAdmin} // Передаем информацию, является ли пользователь админом
                    />
                ))} 
            </div>
        </div>
    );
}
