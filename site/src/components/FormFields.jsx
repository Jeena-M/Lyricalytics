import React from 'react';

function FormFields({
                        username,
                        setUsername,
                        password,
                        setPassword,
                        confirmPassword,
                        setConfirmPassword,
                        showConfirm = false,
                    }) {
    return (
        <div>
            {/* Username Field */}
            <div style={{ marginBottom: '10px' }}>
                <label htmlFor="username">Username</label><br />
                <input
                    id="username"
                    name="username"
                    type="text"
                    value={username}
                    aria-required="true"
                    onChange={(e) => setUsername(e.target.value)}
                    style={{ width: '100%', padding: '8px', marginTop: '4px' }}
                />
            </div>

            {/* Password Field */}
            <div style={{ marginBottom: '10px' }}>
                <label htmlFor="password">Password</label><br />
                <input
                    id="password"
                    name="password"
                    type="password"
                    value={password}
                    aria-required="true"
                    onChange={(e) => setPassword(e.target.value)}
                    style={{ width: '100%', padding: '8px', marginTop: '4px' }}
                />
            </div>

            {/* Confirm Password Field (shown only if showConfirm = true) */}
            {showConfirm && (
                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="confirmPassword">Confirm Password</label><br />
                    <input
                        id="confirmPassword"
                        name="confirmPassword"
                        type="password"
                        value={confirmPassword}
                        aria-required="true"
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        style={{ width: '100%', padding: '8px', marginTop: '4px' }}
                    />
                </div>
            )}
        </div>
    );
}

export default FormFields;