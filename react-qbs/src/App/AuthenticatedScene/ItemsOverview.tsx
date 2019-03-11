import * as React from "react";

import { ItemsSubscription } from "./ItemsSubscription";

export const ItemsOverview = () => (
  <ItemsSubscription>
    {({ loading, error, data }) => (
      <div>
        {error ? (
          <em>{error.message}</em>
        ) : loading ? (
          <p>Loading ...</p>
        ) : (
          <ul>
            {data.items.map((item) => (
              <li key={item.itemId}>{item.body}</li>
            ))}
          </ul>
        )}
      </div>
    )}
  </ItemsSubscription>
);
