import * as React from "react";

import { LoginScene } from "./LoginScene";

interface ILoginSceneContainerProps {
  onAuthentication: (nickname: string) => void;
}

interface ILoginSceneContainerState {
  nickname: string;
}

class LoginSceneContainer extends React.Component<ILoginSceneContainerProps, ILoginSceneContainerState> {
  public state: ILoginSceneContainerState = { nickname: "" };

  public render() {
    return (
      <LoginScene
        nickname={this.state.nickname}
        onAuthentication={this.onAuthentication}
        onNicknameChange={this.onNicknameChange}
      />
    );
  }

  private onAuthentication = (e: React.FormEvent) => {
    // Prevent the default behaviour of submitting a form
    e.preventDefault();
    // Call the prop signalling an authentication
    this.props.onAuthentication(this.state.nickname);
  }

  private onNicknameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    this.setState({ nickname: e.target.value });
  }
}

export { LoginSceneContainer as LoginScene };
