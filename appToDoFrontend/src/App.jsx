import { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import './index.css';
import Header from './components/Header';
import AddNoteComponent from './components/AddNoteComponent';
import NoteModal from './components/NoteModal';

// Define the parseJwt function here
const parseJwt = (token) => {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Failed to parse JWT:", e);
        return null;
    }
};
function App() {

  const [notes, setNotes] = useState([]);   //+
  const [selectedNote, setSelectedNote] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(false);
  const [userEmail, setUserEmail] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);


        useEffect(() => {
            const token = localStorage.getItem('token');
            const storedEmail = localStorage.getItem('userEmail'); // Load email from localStorage

            if (storedEmail) {
                setUserEmail(storedEmail); // Restore email
            }

            setLoading(true);
            if (token) {
                const decodedToken = parseJwt(token);
                if (decodedToken && decodedToken.roles && decodedToken.roles.includes('ROLE_ADMIN')) {
                    setIsAdmin(true); // Set isAdmin based on the token
                }

                setIsAuthenticated(true);
                axios.get('http://localhost:8080/api/notes', {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                })
                    .then(response => {
                        setNotes(response.data);
                    })
                    .catch(error => {
                        console.error('Error fetching notes:', error);
                        setIsAuthenticated(false);
                    })
            .finally(() => {
                    setLoading(false); // Finish loading
                });
            } else {
                setIsAuthenticated(false);
                setLoading(false);
                console.error('No token found, unable to fetch notes.');
            }
        }, []);


    const loadNotes = () => {
        const token = localStorage.getItem('token');

        setLoading(true);
        if (token) {
            axios
                .get('http://localhost:8080/api/notes', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                })
                .then((response) => {
                    setNotes(response.data);
                })
                .catch((error) => {
                    console.error('Error fetching notes:', error);
                    setIsAuthenticated(false);
                })
                .finally(() => {
                    setLoading(false); // Finish loading
                });
        } else {
            setIsAuthenticated(false);
            setLoading(false);
            console.error('No token found, unable to fetch notes.');
        }
    };

  // Modified handleAddNote to send a request to the server
  const handleAddNote = () => {
      const token = localStorage.getItem('token');
    const newNote = { title: '', text: '', isCompleted: false };    // Removed ID creation on the frontend
    axios.post('http://localhost:8080/api/notes', newNote , {
        headers: {
            Authorization: `Bearer ${token}`
        }
    })    // Sending the new note to the server
      .then(response => {
        setNotes([...notes, response.data]);     // Server returns the note with a generated ID
      })
      .catch(error => {
        console.error('Error creating note:', error);
      });
  };

  const handleNoteClick = (note) => {
    setSelectedNote(note);
  };




  // Modified handleDeleteNote to send a request to the server
  const handleDeleteNote = (id) => {
      const token = localStorage.getItem('token');
    axios.delete(`http://localhost:8080/api/notes/${id}`, {
        headers: {
            Authorization: `Bearer ${token}`, // Adding token to the Authorization header
            'Content-Type': 'application/json' // Specifying content type
        }
    })
      .then(() => {
          setNotes(prevNotes => prevNotes.filter(note => note.id !== id));
      })
      .catch(error => {
        console.error('Error deleting note:', error);
      });
  };

    //  Modified handleSaveNote to send a request to the server
    const handleSaveNote = (updatedNote) => {
        const token = localStorage.getItem('token');
      axios.put(`http://localhost:8080/api/notes/${updatedNote.id}`, updatedNote, {
          headers: {
              Authorization: `Bearer ${token}`, // Adding token to the Authorization header
              'Content-Type': 'application/json' // Specifying content type
          }
      })
        .then(response => {
          setNotes(notes.map(note => (note.id === updatedNote.id ? response.data : note)));
          setSelectedNote(null);
        })
        .catch(error => {
          console.error('Error updating note:', error);
        });
    };


    const handleToggleComplete = (id, isCompleted) => {
      const noteToUpdate = notes.find(note => note.id === id);
      const updatedNote = { ...noteToUpdate, isCompleted };
      const token = localStorage.getItem('token');

      axios.put(`http://localhost:8080/api/notes/${id}`, updatedNote, {
          headers: {
              Authorization: `Bearer ${token}`, // Adding token to the Authorization header
              'Content-Type': 'application/json' // Specifying content type
          }
      })
        .then(response => {
          setNotes(notes.map(note => (note.id === id ? response.data : note)));
        })
        .catch(error => {
          console.error('Error updating note completion status:', error);
        });
    };

    const handleRegister = ({ email, password }, onClose) => {
        console.log('handleRegister called with:', { email, password });
        const username = email;
        axios.post('http://localhost:8080/api/register',
            { username, email, password },
            { headers: { 'Content-Type': 'application/json' } }
        )
            .then(response => {
                console.log('Registration successful:', response.data);
                alert('Registration successful! Please log in to continue.');
                //onClose();
            })
            .catch(error => {
                console.error('Error registering:', error);

                // Creating a new Audio object
                const audio = new Audio('./videoplayback (mp3cut.net).m4a');
                audio.play();  // Воспроизводим аудио

                // Extract the error message and display it in an alert
                setTimeout(() => {
                    if (error.response && error.response.data) {
                        alert(error.response.data);  // Displaying an error message
                    } else {
                        alert('An error occurred during registration.');
                    }
                }, 100);
            });
    };

    const handleLogin = ({ email, password }) => {
        axios.post('http://localhost:8080/api/login',
            { email, password },
            { headers: { 'Content-Type': 'application/json' } }
        )
            .then(response => {
                console.log('Login successful:', response.data);
                setIsAuthenticated(true);
                if (response.data.token) {
                    localStorage.setItem('token', response.data.token);
                    const decodedToken = parseJwt(response.data.token);
                    if (decodedToken && decodedToken.sub) {
                        setUserEmail(decodedToken.sub); // Updating the userEmail state
                        localStorage.setItem('userEmail', decodedToken.sub); // Saving email to localStorage
                    }
                    if (decodedToken && decodedToken.roles && decodedToken.roles.includes('ROLE_ADMIN')) {
                        //  If it's an admin, store role information
                        setIsAdmin(true); // Setting the isAdmin state
                    } else {
                        setIsAdmin(false); // If not an admin, reset the isAdmin state
                        console.log('Admin logged in');
                    }
                    loadNotes(); // Loading notes immediately after logging in
                } else {
                    console.error('Token not found in response');
                    alert('An error occurred during login. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error logging in:', error);
                if (error.response && error.response.status === 401) {
                    // If the server returned a 401 Unauthorized status
                    alert('Invalid email or password');
                } else {
                    // Any other errorAny other error
                    alert('An error occurred during login. Please try again.');
                }
            });
    };


    const handleSignin = () => {
        // Logic for handling registration
    };

    const handleLogout = () => {
        setIsAuthenticated(false);
        setNotes([]); // Clearing notes
        setUserEmail(''); // Clearing email from state
        setIsAdmin(false); // Resetting isAdmin state
        localStorage.removeItem('token'); // Removing token from local storage
        localStorage.removeItem('userEmail'); // Removing email from local storage
    };

    if (loading) {
        return <div>Loading...</div>; // Show loading indicator
    }


  return (
      <div>
          {/* Adding background blur */}
          <div className="background-blur"></div>

          {/* Основной контент */}
          <div className="content">
            <Header
                handleAddNote={handleAddNote}
                onSignin={handleSignin}
                onLogin={handleLogin}
                onLogout={handleLogout}
                isAuthenticated={isAuthenticated}
                handleRegister={handleRegister}
                userEmail={userEmail} // Passing email to Header

            />

            <AddNoteComponent
                notes={notes} //+
                handleDeleteNote={handleDeleteNote}//+
                handleNoteClick={handleNoteClick} //+
                handleToggleComplete={handleToggleComplete}
                isAdmin={isAdmin} // Передаем isAdmin
            />

            {selectedNote && (
                <NoteModal
                    note={selectedNote}
                    onSave={handleSaveNote}
                    onClose={() => setSelectedNote(null)}
                />
            )}
          </div>
      </div>
  );
}

export default App;
