const { SlashCommandBuilder } = require("@discordjs/builders");
const { MessageEmbed } = require("discord.js");
const { log } = require("../../utils/logger");
const { notify } = require("../commands");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('remove')
        .setDescription('Удаляет композицию из очереди')
        .addIntegerOption(o => o
            .setName('target')
            .setDescription('Номер в очереди целевой композиции')
            .setRequired(true)),
    async execute(interaction) {
        await remove(interaction);
    },
    async listener(interaction) {}
}

const remove = async (interaction) => {
    if (!interaction.client.queue.songs || interaction.client.queue.songs.length <= 2) {
        const embed = new MessageEmbed()
            .setColor('#ffff00')
            .setTitle('Ты одинок что ли? Соло-игрок?')
            .setDescription(`${interaction.client.queue.songs.length === 0
                ? 'Пытаться перемещать то, чего нет, показывает все твое отчаяние. **Пуст плейлист. Пуст.**'
                : 'В одиночку, конечно, можно получить удовольствие, но двигать то все равно не куда. **Одна песня в плейлисте. Как ты...**'}`)
            .setTimestamp();
        await notify('remove', interaction, {embeds: [embed]});
        log(`[remove] Пропустить композицию не вышло: плеер не играет`);
        return;
    }

    if (interaction.client.queue.connection.joinConfig.channelId !==
        interaction.member.voice.channel.id) {
            const embed = new MessageEmbed()
                .setColor('#ffff00')
                .setTitle('Канал не тот')
                .setDescription(`Мда.. шиза.. перепутать каналы это надо уметь`)
                .setTimestamp();
            await notify('remove', interaction, {embeds: [embed]});
            log(`[remove] Пропустить композицию не вышло: не совпадают каналы`);
            return;
    }

    let targetIndex = interaction.options.getInteger("target") - 1;

    if (targetIndex < 1 || targetIndex + 1 > interaction.client.queue.songs.length) {
        const embed = new MessageEmbed()
            .setColor('#ffff00')
            .setTitle('Ты это.. Вселенной ошибся, чел.')
            .setDescription(`Типа знаешь вселенная расширяется, а твой мозг походу нет. Ну вышел ты за пределы размеров очереди.`)
            .setTimestamp();
        await notify('remove', interaction, {embeds: [embed]});
        log(`[remove] Удалить композицию не вышло: выход за пределы очереди`);
        return;
    }

    interaction.client.queue.songs.splice(interaction.options.getInteger('target') - 1, 1);
    const embed = new MessageEmbed()
        .setColor('#00ff00')
        .setTitle('Целевая композиция дезинтегрирована')
        .setDescription(`Композиция **${target}** была стерта из реальности очереди.`);
    await notify('remove', interaction, {embeds: [embed]});
    log(`[remove] Композиция была успешно удалена из очереди`);
} 