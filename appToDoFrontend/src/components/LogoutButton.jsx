import PropTypes from 'prop-types';
export default function LogoutButton({ onLogout, userEmail  }) {
    return (

        <div className="logoutButtonDiv">
                 <span className="userEmail">{userEmail}</span> {/* Displaying the email */}
                 <button className="logoutButton"  onClick={onLogout}>
                    Logout
                </button>
        </div>
    );
}

LogoutButton.propTypes = {
    onLogout: PropTypes.func.isRequired,
    userEmail: PropTypes.string.isRequired,
};