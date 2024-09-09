

import logo from '/unnamed.png';
import SigninLogin from './SigninLogin';
import LogoutButton from './LogoutButton.jsx';


export default function Header ({handleAddNote, onSignin, onLogin, onLogout, isAuthenticated, handleRegister, userEmail }){


   return(
       <div className="header">

           {isAuthenticated ? (
               <LogoutButton onLogout={onLogout} userEmail={userEmail} /> // Передаем email в LogoutButton
           ) : (
               <SigninLogin onSignin={handleRegister} onLogin={onLogin} />
           )}
           <div className="header-content">
               <img src={logo} alt="Logo" className='logo'/>
               <h1 className="colorMetro">METRO.DIGITAL</h1>
           </div>

           <div >
               <h2 className="ToDoAppAndAddNote" >ToDo App</h2>
               <button className="addNoteButton" onClick={handleAddNote}>Add Note</button>
           </div>
       </div>
   )

}