export default function LogoutButton({ onLogout }) {
    return (
        <button className="logoutButton" onClick={onLogout}>
            Logout
        </button>
    );
}