export const formatEnum = (value: string): string => {
    return value
    .toLowerCase()
    .split("_")
    .map(palabra => palabra.charAt(0).toUpperCase() + palabra.slice(1))
    .join(" ")
}