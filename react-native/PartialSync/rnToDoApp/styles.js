import { StyleSheet} from 'react-native';

export const styles = StyleSheet.create({
    button: {
        backgroundColor: "lightblue",
        padding: 12,
        margin: 16,
        borderRadius: 4,
        borderColor: "rgba(0, 0, 0, 0.1)"
    },
    buttonGroup: {
        flexDirection: 'row',
    },
    container: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
    },
    modalContent: {
        flexDirection: 'column',
        backgroundColor: "white",
        padding: 22,
        justifyContent: "center",
        alignItems: "center",
        borderRadius: 4,
        borderColor: "rgba(0, 0, 0, 0.1)"
    },
    text: {
        textAlign: 'center',
    }
})