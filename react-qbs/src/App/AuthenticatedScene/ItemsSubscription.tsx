import gql from "graphql-tag";
import * as React from "react";
import { Subscription, SubscriptionResult } from "react-apollo";

interface IItem {
  itemId: string;
  body: string;
  isDone: boolean;
  timestamp: Date;
}

interface IData {
  items: IItem[];
}

export const query = gql`
  subscription {
    items {
      itemId
      body
      isDone
      timestamp
    }
  }
`;

interface IItemsSubscriptionProps {
  children: (result: SubscriptionResult<IData>) => React.ReactNode;
}

export const ItemsSubscription = ({ children }: IItemsSubscriptionProps) => (
  <Subscription subscription={query} children={children} />
);
