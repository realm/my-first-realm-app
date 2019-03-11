import * as React from "react";

interface ILoginScenenProps {
  nickname: string;
  onAuthentication: (e: React.FormEvent) => void;
  onNicknameChange: (e: React.ChangeEvent) => void;
}

export const LoginScene = ({ nickname, onAuthentication, onNicknameChange }: ILoginScenenProps) => (
  <form onSubmit={onAuthentication}>
    <input value={nickname} onChange={onNicknameChange} />
    <input type="submit" />
  </form>
);
