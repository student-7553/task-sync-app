const functions = require("firebase-functions");
const admin = require("firebase-admin");
const { Kafka } = require("kafkajs");

admin.initializeApp();

const db = admin.firestore();

// Initialize Kafka client
const kafka = new Kafka({
    clientId: 'firebase-functions-logger',
    brokers: process.env.KAFKA_BROKERS ? process.env.KAFKA_BROKERS.split(',') : ['localhost:9092']
});

const producer = kafka.producer();

// Connect producer once 
let isProducerConnected = false;
async function connectProducer() {
    if (!isProducerConnected) {
        await producer.connect();
        isProducerConnected = true;
    }
}

async function logToKafka(functionName, data) {
    try {
        await connectProducer();
        const message = {
            function: functionName,
            timestamp: new Date().toISOString(),
            data: data
        };
        await producer.send({
            topic: process.env.KAFKA_TOPIC || 'firebase-function-logs',
            messages: [
                { value: JSON.stringify(message) }
            ],
        });
        console.log(`Successfully logged ${functionName} to Kafka`);
    } catch (error) {
        console.error("Failed to log to Kafka:", error);
    }
}

exports.saveTask = functions.https.onCall(async (data, context) => {
    await logToKafka('saveTask', { userId: data.userId, taskId: data.task?.id });
    try {
        const userId = data.userId;
        const task = data.task;

        if (!task || !task.id) {
            throw new functions.https.HttpsError('invalid-argument', 'The task must have an id.');
        }

        const taskToSave = {
            ...task,
            isSynced: true
        };

        await db.collection("tasks").doc(task.id).set(taskToSave);
        return { success: true };
    } catch (error) {
        console.error("Error saving task:", error);
        throw new functions.https.HttpsError('internal', error.message);
    }
});

exports.getTasks = functions.https.onCall(async (data, context) => {
    await logToKafka('getTasks', { userId: data.userId });
    try {
        const userId = data.userId;
        const snapshot = await db.collection("tasks").get();
        const tasks = [];
        snapshot.forEach(doc => {
            tasks.push(doc.data());
        });
        return tasks;
    } catch (error) {
        console.error("Error getting tasks:", error);
        throw new functions.https.HttpsError('internal', error.message);
    }
});

exports.deleteTask = functions.https.onCall(async (data, context) => {
    await logToKafka('deleteTask', { taskId: data.taskId });
    try {
        const taskId = data.taskId;
        if (!taskId) {
            throw new functions.https.HttpsError('invalid-argument', 'taskId is required.');
        }

        await db.collection("tasks").doc(taskId).delete();
        return { success: true };
    } catch (error) {
        console.error("Error deleting task:", error);
        throw new functions.https.HttpsError('internal', error.message);
    }
});
