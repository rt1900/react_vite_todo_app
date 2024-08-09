import { useState } from 'react';

export default function SignInModal({ onClose, onRegister }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [captcha, setCaptcha] = useState('')
    const handleSubmit = (e) => {
        e.preventDefault();
        onRegister({ email, password, captcha });
    };
    return (
        <div className="SignInModal">
            <div className="signin-modal-content">
                <h2>Register</h2>
                <form onSubmit={handleSubmit}>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <label>Enter the numbers from the image:</label>
                    <input
                        type="text"
                        value={captcha}
                        onChange={(e) => setCaptcha(e.target.value)}
                        required
                    />
                    {/* Здесь должно быть изображение с капчей */}
                    <p>If you don't see an image, then I'm sorry, I have bad news for you, you are unfortunately a robot. Maybe you didn't know that. Just kidding, dear human. Just click on "Register".</p>
                    <button type="submit">Register</button>
                </form>
                <button onClick={onClose}>Close</button>
            </div>
        </div>
    );
}
