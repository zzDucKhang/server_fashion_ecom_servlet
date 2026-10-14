// src/components/ui/form-field.tsx
import React from 'react';
import { Control, Controller, FieldValues, Path } from 'react-hook-form';

interface FormFieldProps<T extends FieldValues> {
  control: Control<T>;
  name: Path<T>;
  label: string;
  type?: string;
  placeholder?: string;
  error?: string;
}

export function FormField<T extends FieldValues>({
  control,
  name,
  label,
  type = 'text',
  placeholder,
  error,
}: FormFieldProps<T>) {
  return (
    <div className="space-y-1">
      <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
        {label}
      </label>
      <Controller
        name={name}
        control={control}
        render={({ field }) => (
          <input
            {...field}
            type={type}
            placeholder={placeholder}
            className={`w-full rounded-xl border px-4 py-2.5 text-sm outline-none transition focus:border-neutral-900 ${
              error ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF] bg-white'
            }`}
          />
        )}
      />
      {error && <p className="text-xs text-red-500">{error}</p>}
    </div>
  );
}