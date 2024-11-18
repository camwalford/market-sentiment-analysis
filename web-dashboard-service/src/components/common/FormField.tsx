import React from "react";

interface FormFieldProps {
    id: string;
    name: string;
    type: string;
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    placeholder: string;
    required?: boolean;
    autoComplete?: string;
    isFirst?: boolean;
    isLast?: boolean;
}

const FormField: React.FC<FormFieldProps> = ({
                                                 id,
                                                 name,
                                                 type,
                                                 value,
                                                 onChange,
                                                 placeholder,
                                                 required = true,
                                                 autoComplete,
                                                 isFirst,
                                                 isLast
                                             }) => {
    const roundedClasses = `
        ${isFirst ? 'rounded-t-md' : ''}
        ${isLast ? 'rounded-b-md' : ''}
        ${!isFirst && !isLast ? '' : ''}
    `;

    return (
        <div>
            <label htmlFor={id} className="sr-only">
                {placeholder}
            </label>
            <input
                id={id}
                name={name}
                type={type}
                required={required}
                value={value}
                onChange={onChange}
                autoComplete={autoComplete}
                className={`
                    appearance-none rounded-none relative block
                    w-full px-3 py-2 border border-gray-300
                    placeholder-gray-500 text-gray-900
                    focus:outline-none focus:ring-blue-500
                    focus:border-blue-500 focus:z-10 sm:text-sm
                    ${roundedClasses}
                `}
                placeholder={placeholder}
            />
        </div>
    );
};

export default FormField;