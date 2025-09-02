import React from 'react';
function CreateButton({ onClick, children = 'Create', style = {}, ...props }) {
    const defaultStyle = {
        backgroundColor: '#3F51B5',
        color: '#fff',
        padding: '10px 20px',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
    };

    return (
        <button
            type={props.type || 'submit'}
            onClick={onClick}
            style={{ ...defaultStyle, ...style }}
            aria-label={props['aria-label'] || children}
            {...props}
        >
            {children}
        </button>
    );
}

export default CreateButton;