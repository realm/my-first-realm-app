export const taskSchema = {
    name: 'task',
    properties: {
        taskID: 'string',
        owner: 'string',
        name: 'string',
    }
}

export const Project = {
    name: 'Project',
    primaryKey: 'projectId',
    properties: {
      projectId: 'string',
      owner: 'string',
      name: 'string',
      timestamp: 'date',
      items: 'Item[]'
    }
}

export const Item = {
    name: 'Item',
    primaryKey: 'itemId',
    properties: {
      itemId: 'string',
      body: 'string',
      isDone: 'bool',
      timestamp: 'date'
    }
}