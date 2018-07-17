export const projectSchema = {
    name: 'project',
    properties: {
        projectID: 'string',
        owner: 'string',
        name: 'string',
        createdAt: 'date',
        tasks: { type: 'list', objectType: 'task' },



    }
}