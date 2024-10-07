
import PropTypes from 'prop-types';
import logo from '/unnamed.png';
import SigninLogin from './SigninLogin';
import LogoutButton from './LogoutButton.jsx';


export default function Header ({handleAddNote, /*onSignin*/ onLogin, onLogout, isAuthenticated, handleRegister, userEmail }){


   return(
       <div className="header">

           {isAuthenticated ? (
               <LogoutButton onLogout={onLogout} userEmail={userEmail} /> // Passing email to LogoutButton
           ) : (
               <SigninLogin onSignin={handleRegister} onLogin={onLogin} />
           )}
           <div className="header-content">
               <img src={logo} alt="Logo" className='logo'/>
               <h1 className="colorMetro">METRO.DIGITAL</h1>
           </div>

           <div >
               <h2 className="ToDoAppAndAddNote" >TODO</h2>
               {isAuthenticated && (
                   <button className="addNoteButton" onClick={handleAddNote}>
                       Add Note
                   </button>
               )}
           </div>
       </div>
   )

}


Header.propTypes = {
    handleAddNote: PropTypes.func.isRequired,
    onLogin: PropTypes.func.isRequired,
    onLogout: PropTypes.func.isRequired,
    isAuthenticated: PropTypes.bool.isRequired,
    handleRegister: PropTypes.func.isRequired,
    userEmail: PropTypes.string,
};