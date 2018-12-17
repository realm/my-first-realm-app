export const Project = {
  name: "Project",
  primaryKey: "projectId",
  properties: {
    projectId: "string",
    owner: "string",
    name: "string",
    timestamp: "date",
    items: "Item[]",
    permissions: "__Permission[]"
  }
};

export const Item = {
  name: "Item",
  primaryKey: "itemId",
  properties: {
    itemId: "string",
    body: "string",
    isDone: "bool",
    timestamp: "date"
  }
};
