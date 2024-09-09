export default function LogoutButton({ onLogout, userEmail  }) {
    return (

        <div className="logoutButtonDiv">
                 <span className="userEmail">{userEmail}</span> {/* Отображаем email */}
                 <button className="logoutButton"  onClick={onLogout}>
                    Logout
                </button>
        </div>
    );
}