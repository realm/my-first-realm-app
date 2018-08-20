import React, { Component } from 'react';
import { View, Text, TextInput, TouchableOpacity, ListView } from 'react-native';
import { List, ListItem } from 'react-native-elements';

class PageView extends Component {
    state = {
        dataSource: this.props.dataSource
    }

    componentWillReceiveProps(newProps) {
        console.log('received new props')
        console.log(newProps)
        this.setState({ dataSource: newProps.dataSource })
    }

    renderRow(data) {
        return(
            <TouchableOpacity>
                <ListItem
                    key={data.taskID}
                    title={data.name}
                    hideChevron
                />
            </TouchableOpacity>
        );
    }
    renderList() {
        const { dataSource } = this.state

        if (dataSource) {
            return(
                <List>
                    <ListView 
                        enableEmptySections
                        renderRow={this.renderRow.bind(this)}
                        dataSource={dataSource}
                    />
                </List>
            );
        }
        return(
            <View>
                <Text>
                    {this.props.placeholder}
                </Text>
            </View>
        );
    }
    render() {
        return(
            <View>
                {this.renderList()}
            </View>
        );
    }
}

export default PageView;